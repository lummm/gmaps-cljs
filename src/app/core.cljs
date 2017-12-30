(ns app.core
  (:require [reagent.dom :refer [render]]
            [re-frame.core :refer [dispatch-sync]]
            [app.routes :as routes]
            [app.views :as views]
            [app.handlers.core]
            [app.subs.core]
            [weasel.repl :as repl]
            ))

(when-not (repl/alive?)
  (repl/connect "ws://localhost:9001"))

(defn mount-root []
  (render [views/main] (.getElementById js/document "app")))

(defn ^:export init []
  (routes/start!)
  (dispatch-sync [:initialize-db])
  (mount-root))
