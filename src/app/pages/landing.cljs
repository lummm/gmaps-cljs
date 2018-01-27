(ns app.pages.landing
  (:require [reagent-material-ui.core :as ui]
            [app.gmaps.core :as gmaps]))

(defn page []
  (fn []
    [:div
     [:img.w-100
      {:src "img/vietnam-bg.jpg"}]
     [gmaps/home ]]))
