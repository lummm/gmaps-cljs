(ns app.pages.landing
  (:require [reagent-material-ui.core :as ui]
            [app.gmaps.core :as gmaps]))

(defn page []
  (fn []
    [:div.w-100pe.h-100 {:style {:background-image "url('img/vietnam-bg.jpg')"
                                 :background-size "cover"}}
     [:div.h-100.flex-column
      [:div.h-50]
      [gmaps/map-component ]]
     ]))
