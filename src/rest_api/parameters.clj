(ns rest-api.parameters)

(def get-users
  {:query
   [:map [:last-name {:optional true} string?]]})

(def post-user
  {:body [:map
          [:user-name string?]
          [:first-name string?]
          [:last-name string?]]})

(def get-user
  {:path [:map
          [:user-id pos-int?]]})

(def put-user
  {:path [:map
          [:user-id pos-int?]]
   :body [:map
          [:user-name string?]
          [:first-name string?]
          [:last-name string?]]})

(def patch-user
  {:path [:map
          [:user-id pos-int?]]
   :body [:map
          [:user-name string?]
          [:first-name string?]
          [:last-name string?]]})

(def delete-user
  {:path [:map
          [:user-id pos-int?]]})