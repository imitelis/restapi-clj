(ns rest-api.handlers
  (:require [clojure.string :as string]))

(defn get-users
  [last-name users]
  (if (some? last-name)
    {:status 200 :body {:users (->> @users
                                    vals
                                    (filter #(string/starts-with?
                                              (:last-name %)
                                              last-name)))}}
    {:status 200 :body {:users (vals @users)}}))

(defn post-user
  [user user-name users next-user-id]
  (let [user-exists? (->> (vals @users)
                          (filter #(= (:user-name %) user-name))
                          (seq)
                          (some?))]
    (if user-exists?
      {:status 400 :body {:error "user-name already exists"}}
      (let [user-id  (swap! next-user-id inc)
            new-user (assoc user :user-id user-id)]
        (swap! users assoc user-id new-user)
        {:status 201 :body {:user new-user}}))))

(defn get-user
  [user-id users]
  (if-let [user (get @users user-id)]
    {:status 200 :body {:user user}}
    {:status 400 :body {:error "user-id does not exists"}}))

(defn put-user
  [user-id body users]
  (if-let [user (get @users user-id)]
    (let [updated-user (merge user body)]
      (swap! users assoc user-id updated-user)
      {:status 200 :body {:user updated-user}})
    {:status 400 :body {:error "user-id does not exists"}}))

(defn patch-user
  [user-id body users]
  (if-let [user (get @users user-id)]
    (let [updated-user (merge user body)]
      (swap! users assoc user-id updated-user)
      {:status 200 :body {:user updated-user}})
    {:status 400 :body {:error "user-id does not exists"}}))

(defn delete-user
  [user-id users]
  (if-let [user (get @users user-id)]
    (do
      (swap! users dissoc user-id)
      {:status 200 :body {:user user}})
    {:status 400 :body {:error "user-id does not exists"}}))