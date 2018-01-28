(ns app.handlers.core
  (:require [re-frame.core :refer [register-handler]]
            [app.db :as db]
            [app.components.db :refer [component-dbs]]
            [app.gmaps.core :as gmaps]))

(register-handler
  :initialize-db
  (fn  [_ _]
    (reduce merge
            (concat [db/default-db]
                    (component-dbs)
                    [gmaps/default-db]))))

(register-handler
  :route
  (fn [db [_ {:keys [current-page route-params]}]]
    (merge db {:current-page current-page :route-params route-params})))
