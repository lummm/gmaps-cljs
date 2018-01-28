(ns app.gmaps.core
  (:require [reagent.core :as r]))

(defn- map-render []
  [:div.h-50
   ])

(defn- map-did-mount [this]
  (let [lat-long (js/google.maps.LatLng. 49.28, -123.09)
        map-canvas (r/dom-node this)
        map-options (clj->js {"center" lat-long
                              "zoom" 12})
        map-comp (js/google.maps.Map. map-canvas map-options)
        marker (js/google.maps.Marker. (clj->js {"position" lat-long
                                                 "map" map-comp}))]
    ))

(defn map-component []
  (r/create-class {:reagent-render map-render
                   :component-did-mount map-did-mount}))
