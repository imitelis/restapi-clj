(ns rest-api.middleware-test
  (:require [clojure.test :refer [deftest testing is]]
            [camel-snake-kebab.core :as csk]
            [rest-api-reitit.middleware :as middleware]))

(deftest convert-letter-case-test
  (testing "Testing middleware `convert-letter-case` function"
    (is (= (middleware/convert-letter-case csk/->camelCaseKeyword "camel-case") :camelCase))
    (is (= (middleware/convert-letter-case csk/->snake_case "snake-case") "snake_case"))
    (is (= (middleware/convert-letter-case csk/->camelCaseKeyword true) :true))
    (is (= (middleware/convert-letter-case csk/->kebab-case-keyword 200) :200))
    (is (nil? (middleware/convert-letter-case csk/->camelCaseKeyword nil)))
    (is (nil? (middleware/convert-letter-case csk/->camelCaseKeyword {})))))

(deftest letter-case-keyword-test
  (testing "Testing middleware `Letter-case-keyword` function"
    (is (= (middleware/letter-case-keyword "Pascal_Case" :PascalCase) :PascalCase))
    (is (= (middleware/letter-case-keyword "pascal-case" :PascalCase) :PascalCase))
    (is (= (middleware/letter-case-keyword :pascal-case  :PascalCase) :PascalCase))
    (is (= (middleware/letter-case-keyword "camelCase"  :camelCase) :camelCase))
    (is (= (middleware/letter-case-keyword "camel-case" :camelCase) :camelCase))
    (is (= (middleware/letter-case-keyword :camel-case  :camelCase) :camelCase))
    (is (= (middleware/letter-case-keyword "screaming snake case" :SCREAMING_SNAKE_CASE) :SCREAMING_SNAKE_CASE))
    (is (= (middleware/letter-case-keyword "screaming-snake-case" :SCREAMING_SNAKE_CASE) :SCREAMING_SNAKE_CASE))
    (is (= (middleware/letter-case-keyword :Screaming_Snake_Case  :SCREAMING_SNAKE_CASE) :SCREAMING_SNAKE_CASE))
    (is (= (middleware/letter-case-keyword "snake case" :snake_case) :snake_case))
    (is (= (middleware/letter-case-keyword "snake-case" :snake_case) :snake_case))
    (is (= (middleware/letter-case-keyword :snakeCase  :snake_case) :snake_case))
    (is (= (middleware/letter-case-keyword "kebab case" :kebab-case) :kebab-case))
    (is (= (middleware/letter-case-keyword "kebab_case" :kebab-case) :kebab-case))
    (is (= (middleware/letter-case-keyword :kebabCase   :kebab-case) :kebab-case))
    (is (= (middleware/letter-case-keyword "camel snake case" :Camel_Snake_Case) :Camel_Snake_Case))
    (is (= (middleware/letter-case-keyword "camel-snake-case" :Camel_Snake_Case) :Camel_Snake_Case))
    (is (= (middleware/letter-case-keyword :camel-snake-case  :Camel_Snake_Case) :Camel_Snake_Case))
    (is (= (middleware/letter-case-keyword "test" :test) :test))))

(deftest filter-keys-letter-case-test
  (testing "Testing middleware `filter-keys-letter-case` function"
    (is (= (middleware/filter-keys-letter-case {:userName "user" :first-name "name"} :camelCase)
           {:userName "user"}))
    (is (= (middleware/filter-keys-letter-case {:userName "user" :first-name "name"} :kebab-case)
           {:first-name "name"}))
    (is (= (middleware/filter-keys-letter-case {:userName "user" :first-name "name"} :snake_case)
           {}))
    (is (= (middleware/filter-keys-letter-case {:UserName "user" :first_name "name"} :PascalCase)
           {:UserName "user"}))
    (is (= (middleware/filter-keys-letter-case {:UserName "user" :first_name "name"} :snake_case)
           {:first_name "name"}))))

(deftest transform-keys-letter-case-test
  (testing "Testing middleware `transform-keys-letter-case` function"
    (is (= (middleware/transform-keys-letter-case {:userId 1234 :userName "name"} :kebab-case)
           {:user-id 1234 :user-name "name"}))
    (is (= (middleware/transform-keys-letter-case {:userId 1234 :userName "name"} :snake_case)
           {:user_id 1234 :user_name "name"}))
    (is (= (middleware/transform-keys-letter-case {"userId" 1234 "userName" "name"} :test)
           {:userId 1234 :userName "name"}))))

(deftest transform-request-params-test
  (testing "Testing middleware `transform-request-params` function"
    (is (= (middleware/transform-request-params {:body-params {:userId    1234
                                                               :userName  "user-name"
                                                               :firstName "fname"
                                                               :lastName  "lname"}}
                                                {:from :camelCase :to :kebab-case})
           {:body-params {:user-id    1234
                          :user-name  "user-name"
                          :first-name "fname"
                          :last-name  "lname"}}))
    (is (= (middleware/transform-request-params {:body {:userId    1234
                                                        :userName  "user-name"
                                                        :firstName "fname"
                                                        :lastName  "lname"}}
                                                {:from :camelCase :to :PascalCase})
           {:body {:UserId    1234
                   :UserName  "user-name"
                   :FirstName "fname"
                   :LastName  "lname"}}))))

(deftest letter-case-request-test
  (testing "Testing middleware `letter-case-request` function"
    (let [handler  (middleware/letter-case-request identity {:from :camelCase :to :kebab-case})
          request  {:body-params {:userId    1234
                                  :userName  "user-name"
                                  :firstName "fname"
                                  :lastName  "lname"}}
          response (handler request)]
      (is (= (:body-params response)
             {:user-id 1234 :user-name "user-name" :first-name "fname" :last-name "lname"})))
    (let [handler  (middleware/letter-case-request identity {:from :camelCase :to :snake_case})
          request  {:body-params {:userId    1234
                                  :userName  "user-name"
                                  :firstName "fname"
                                  :lastName  "lname"}}
          response (handler request)]
      (is (= (:body-params response)
             {:user_id 1234 :user_name "user-name" :first_name "fname" :last_name "lname"})))
    (let [handler  (middleware/letter-case-request identity {:from :snake_case :to :PascalCase})
          request  {:body-params {:user_id    1234
                                  :user_name  "user-name"
                                  :first_name "fname"
                                  :last_name  "lname"
                                  :test-key   "ignore"}}
          response (handler request)]
      (is (= (:body-params response)
             {:UserId 1234 :UserName "user-name" :FirstName "fname" :LastName "lname"})))
    (let [handler  (middleware/letter-case-request identity {:from :snake_case :to :PascalCase})
          request  {:body-params  {:user_id    1234
                                   :user_name  "user-name"
                                   :first_name "fname"
                                   :last_name  "lname"}
                    :query-params {:last_name "query"}}
          response (handler request)]
      (is (= response
             {:body-params  {:UserId    1234
                             :UserName  "user-name"
                             :FirstName "fname"
                             :LastName  "lname"}
              :query-params {:LastName "query"}}))))
  (testing "Testing middleware `letter-case-request` function async execution"
    (let [handler (middleware/letter-case-request (fn [request respond _]
                                                    (respond request))
                                                  {:from :camelCase :to :kebab-case})
          request {:body-params {:userId    1234
                                 :userName  "user-name"
                                 :firstName "fname"
                                 :lastName  "lname"}}
          respond (promise)]
      (handler request respond (promise))
      (is (= (:body-params @respond)
             {:user-id 1234 :user-name "user-name" :first-name "fname" :last-name "lname"})))))

(deftest letter-case-response-test
  (testing "Testing middleware `letter-case-response` function"
    (let [handler  (middleware/letter-case-response identity {:to :camelCase})
          response {:body {:new-user {:user-id    1234
                                      :user-name  "user-name"
                                      :first-name "fname"
                                      :last-name  "lname"}}}

          new-body (:body (handler response))]
      (is (= new-body {:newUser {:userId    1234
                                 :userName  "user-name"
                                 :firstName "fname"
                                 :lastName  "lname"}}))))
  (testing "Testing middleware `letter-case-response` function async execution"
    (let [handler  (middleware/letter-case-response (fn [request respond _]
                                                      (respond request))
                                                    {:to :camelCase})
          response {:body {:new-user {:user-id    1234
                                      :user-name  "user-name"
                                      :first-name "fname"
                                      :last-name  "lname"}}}
          respond  (promise)]
      (handler response respond (promise))
      (is (= (:body @respond) {:newUser {:userId    1234
                                         :userName  "user-name"
                                         :firstName "fname"
                                         :lastName  "lname"}})))))
