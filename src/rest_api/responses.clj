(ns rest-api.responses)

(def get-users
  {200 {:body [:map
               [:users
                [:or
                 [:sequential [:map
                               [:user-id pos-int?]
                               [:user-name string?]
                               [:first-name string?]
                               [:last-name string?]]]
                 empty?]]]}})

(def post-user
  {201 {:body [:map
               [:user
                [:map
                 [:user-id pos-int?]
                 [:user-name string?]
                 [:first-name string?]
                 [:last-name string?]]]]}
   400 {:body [:map [:error string?]]}})

(def get-user
  {200 {:body [:map
               [:user
                [:map
                 [:user-id pos-int?]
                 [:user-name string?]
                 [:first-name string?]
                 [:last-name string?]]]]}
   400 {:body [:map [:error string?]]}})

(def put-user
  {200 {:body [:map
               [:user
                [:map
                 [:user-id pos-int?]
                 [:user-name string?]
                 [:first-name string?]
                 [:last-name string?]]]]}
   400 {:body [:map [:error string?]]}})

(def patch-user
  {200 {:body [:map
               [:user
                [:map
                 [:user-id pos-int?]
                 [:user-name string?]
                 [:first-name string?]
                 [:last-name string?]]]]}
   400 {:body [:map [:error string?]]}})

(def delete-user
  {200 {:body [:map
               [:user
                [:map
                 [:user-id pos-int?]
                 [:user-name string?]
                 [:first-name string?]
                 [:last-name string?]]]]}
   400 {:body [:map [:error string?]]}})