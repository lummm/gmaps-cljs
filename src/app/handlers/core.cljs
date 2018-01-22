(ns app.handlers.core
  (:require [re-frame.core :refer [register-handler]]
            [app.db :as db]
            [app.components.db :refer [component-dbs]]))

(register-handler
  :initialize-db
  (fn  [_ _]
    (reduce merge
            (concat [db/default-db]
                    (component-dbs)))))

(register-handler
  :route
  (fn [db [_ {:keys [current-page route-params]}]]
    (merge db {:current-page current-page :route-params route-params})))
