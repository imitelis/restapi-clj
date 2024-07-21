(ns rest-api.server
  (:require [muuntaja.core :as m]
            [malli.util :as mu]
            [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [ring.adapter.jetty :as jetty]
            [rest-api.middleware :as middleware]
            [rest-api.parameters :as parameter]
            [rest-api.responses :as response]
            [rest-api.handlers :as handler]))

;;; users "data-model": for simplicity just hold collection of users in an atom
;;; so we can "add", "remove" and "update"" users in the collection
(def users (atom {1 {:user-id 1 :user-name "user-1" :first-name "one" :last-name "first"}
                  2 {:user-id 2 :user-name "user-2" :first-name "two" :last-name "second"}}))

(def next-user-id (atom (count @users)))

(def app
  (ring/ring-handler
   (ring/router
    [["/_/swagger.json"
      {:get {:no-doc  true
             :swagger {:info {:title       "Users REST API example"
                              :description "Using \"camelCase\" for REST API schema and \"kebab-case\" for back-end code"}
                       :tags [{:name "users" :description "Manage system users"}]}
             :handler (middleware/letter-case-swagger-response
                       (swagger/create-swagger-handler)
                       {:to :camelCase})}}]

     ["/api"
      ["/users"
       {:swagger {:tags ["users"]}
        :get     {:summary    "List all users (or optionally filter using last name)"
                  :parameters parameter/get-users
                  :responses  response/get-users
                  :handler    (fn [{{{:keys [last-name]} :query} :parameters}]
                                (handler/get-users last-name users))}
        :post    {:summary    "Add new user"
                  :parameters parameter/post-user
                  :responses  response/post-user
                  :handler    (fn [{{{:keys [user-name] :as user} :body} :parameters}]
                                (handler/post-user user user-name users next-user-id))}}]

      ["/users/{user-id}"
       {:swagger {:tags ["users"]}
        :get     {:summary    "Retrieve a user"
                  :parameters parameter/get-user
                  :responses  response/get-user
                  :handler    (fn [{{{:keys [user-id]} :path} :parameters}]
                                (handler/get-user user-id users))}
        :put     {:summary    "Update a user"
                  :parameters parameter/put-user
                  :responses  response/put-user
                  :handler    (fn [{{:keys [body] {:keys [user-id]} :path} :parameters}]
                                (handler/put-user user-id body users))}
        :patch     {:summary    "Patch a user"
                    :parameters parameter/patch-user
                    :responses  response/patch-user
                    :handler    (fn [{{:keys [body] {:keys [user-id]} :path} :parameters}]
                                  (handler/patch-user user-id body users))}
        :delete  {:summary    "Delete a user"
                  :parameters parameter/delete-user
                  :responses  response/delete-user
                  :handler    (fn [{{{:keys [user-id]} :path} :parameters}]
                                (handler/delete-user user-id users))}}]]]

    {:data {:coercion   (reitit.coercion.malli/create
                         {:error-keys       #{:value :humanized}
                          :compile          mu/closed-schema
                          :strip-extra-keys false})
            :muuntaja   m/instance
            :middleware [; query-params & form-params
                         parameters/parameters-middleware
                         ; content-negotiation
                         muuntaja/format-negotiate-middleware
                         ; encoding response body
                         muuntaja/format-response-middleware
                         ; decoding request body
                         muuntaja/format-request-middleware
                         ; exception handling
                         exception/exception-middleware
                         ; coerce response body to keys to camelCase
                         [middleware/letter-case-response {:to :camelCase}]
                         ; coerce request params letter case
                         [middleware/letter-case-request {:from :camelCase :to :kebab-case}]
                         ; coercing response body
                         coercion/coerce-response-middleware
                         ; coercing request parameters
                         coercion/coerce-request-middleware]}})

   (ring/routes
    (ring/redirect-trailing-slash-handler {:method :strip})

    (swagger-ui/create-swagger-ui-handler
     {:path   "/api-docs"
      :url    "/_/swagger.json"
      :config {:validatorUrl     nil
               :operationsSorter "alpha"}})

    (ring/create-default-handler
     {:not-authorized     (constantly {:status 401, :body "not-authorized"})
      :not-found          (constantly {:status 404, :body "not-found"})
      :method-not-allowed (constantly {:status 405, :body "not-allowed"})
      :not-acceptable     (constantly {:status 406, :body "not-acceptable"})}))))

;;; http server state (start\stop)
(defonce http-server (atom nil))

(defn start []
  (reset! http-server (jetty/run-jetty #'app {:port 3000 :join? false})))

(defn stop []
  (when-not (nil? @http-server)
    (.stop @http-server)))

(comment
  (start))