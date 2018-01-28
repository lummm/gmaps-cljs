(ns app.gmaps.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-material-ui.core :as ui])
  (:require-macros [reagent.ratom :refer [reaction]]))

(def default-location (js/google.maps.LatLng. 49.28, -123.09))
(def map-ref (atom {}))

;; db
(def default-db
  {::search-text nil})

;; subs
(rf/reg-sub-raw
 ::search-text
 (fn [db]
   (reaction (-> @db ::search-text))))

;; handlers
(rf/register-handler
 ::set-search-text
 (fn [db [_ search-text]]
   (assoc-in db [::search-text ] search-text)))

;; components
(defn- map-render []
  [:div.h-50
   ])

(defn- map-did-mount [this]
  (let [map-canvas (r/dom-node this)
        map-options (clj->js {"center" default-location
                              "zoom" 12})
        map-comp (js/google.maps.Map. map-canvas map-options)
        marker (js/google.maps.Marker. (clj->js {"position" default-location
                                                 "map" map-comp}))]
    (js/console.log marker)
    ;; (reset! map-ref map-comp)
    ))

(defn- map-component []
  (r/create-class {:reagent-render map-render
                   :component-did-mount map-did-mount}))

;; (defn- search []
;;   (let [search-text @(rf/subscribe [::search-text ])
;;         gmaps-searcher (js/google.maps.places.PlacesService. @map-ref)
;;         search-fn (goog.object/get gmaps-searcher "textSearch")
;;         request (clj->js {"location" default-location
;;                           "radius" 500
;;                           "query" search-text})]
;;     (js/console.log gmaps-searcher)
;;     (js/console.log search-text)
;;     (search-fn request (fn [response] (js/console.log response)))))

(defn component []
  [:div.h-100.flex.flex-column
   [:div.h-50.flex.flex-column.justify-end
    [:div.bg-white.ph3
     [ui/TextField {:id :map-search
                    :name "Map search"
                    :hintText "Venue name..."
                    :hintStyle {:color "rgba(0, 0, 0, 0.3)"}
                    :inputStyle {:color "rgba(0, 0, 0, 0.9)"}
                    :onChange (fn [event new-val] (rf/dispatch [::set-search-text new-val]))
                    }]
     [:span.mh3 [ui/RaisedButton {:label "Search"
                                  ;; :onClick #'search
                                  }]]]]
   [map-component ]])
