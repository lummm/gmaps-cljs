(ns app.gmaps.core
  (:require [reagent.core :as r]))

(defn home-render []
  [:div {:style {:height "300px"}}
   ])

(defn home-did-mount [this]
  (let [map-canvas (r/dom-node this)
        map-options (clj->js {"center" (js/google.maps.LatLng. -34.397, 150.644)
                              "zoom" 8})]
    (js/google.maps.Map. map-canvas map-options)))

(defn home []
  (r/create-class {:reagent-render home-render
                         :component-did-mount home-did-mount}))
