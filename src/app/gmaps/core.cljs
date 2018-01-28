(ns app.gmaps.core
  (:require [reagent.core :as r]))

(defn home-render []
  [:div {:style {:height "300px"}}
   ])

(defn home-did-mount [this]
  (let [lat-long (js/google.maps.LatLng. 49.28, -123.09)
        map-canvas (r/dom-node this)
        map-options (clj->js {"center" lat-long
                              "zoom" 12})
        map (js/google.maps.Map. map-canvas map-options)
        marker (js/google.maps.Marker. (clj->js {"position" lat-long
                                                 "map" map}))]
    ))

(defn home []
  (r/create-class {:reagent-render home-render
                   :component-did-mount home-did-mount}))
