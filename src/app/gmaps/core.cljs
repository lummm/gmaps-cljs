(ns app.gmaps.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-material-ui.core :as ui])
  (:require-macros [reagent.ratom :refer [reaction]]))

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
  (let [lat-long (js/google.maps.LatLng. 49.28, -123.09)
        map-canvas (r/dom-node this)
        map-options (clj->js {"center" lat-long
                              "zoom" 12})
        map-comp (js/google.maps.Map. map-canvas map-options)
        marker (js/google.maps.Marker. (clj->js {"position" lat-long
                                                 "map" map-comp}))]
    ))

(defn- map-component []
  (r/create-class {:reagent-render map-render
                   :component-did-mount map-did-mount}))

(defn- search []
  (let [search-text @(rf/subscribe [::search-text ])]
    (js/console.log search-text)))

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
                                  :onClick #'search}]]]]
   [map-component ]])
