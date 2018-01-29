(ns app.gmaps.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-material-ui.core :as ui])
  (:require-macros [reagent.ratom :refer [reaction]]))

(def default-location (js/google.maps.LatLng. 49.28, -123.09))
(def map-ref (atom {}))

(defn make-info-display [marker]
  (let [current-results @(rf/subscribe [::search-result ])
        relevant-result (first
                         (filter
                          #(-> %
                               (goog.object/get "geometry")
                               (goog.object/get "location")
                               (= (goog.object/get marker "position")))
                          current-results))]
    ;; ping the places api when you search to get a photo of the place at the same time
    (str
     "<div>"
     "  <h1>" (goog.object/get relevant-result "name") "</h1>"
     "  <img src='" (goog.object/get relevant-result "icon") "' />"
     "  <p>" (goog.object/get relevant-result "formatted_address") "</p>"
     "</div>")))

;; db
(def default-db
  {::search-text nil
   ::search-result nil
   ::markers nil})

;; subs
(rf/reg-sub-raw
 ::markers
 (fn [db]
   (reaction (-> @db ::markers))))

(rf/reg-sub-raw
 ::search-text
 (fn [db]
   (reaction (-> @db ::search-text))))

(rf/reg-sub-raw
 ::search-result
 (fn [db]
   (reaction (-> @db ::search-result))))

;; handlers
(rf/register-handler
 ::set-markers
 (fn [db [_ new-markers]]
   (let [current-markers (-> db ::markers)]
     (when-not (empty? current-markers)
       (doall (map #(js-invoke % "setMap" nil)
                   current-markers))))
   (doall
    (map (fn [marker]
           (js-invoke
            marker "addListener" "click"
            (fn [] (js-invoke (js/google.maps.InfoWindow. (clj->js {"content" (make-info-display marker)}))
                              "open" @map-ref marker))))
         new-markers))
   (assoc-in db [::markers ] new-markers)))

(rf/register-handler
 ::set-search-text
 (fn [db [_ search-text]]
   (assoc-in db [::search-text ] search-text)))

(rf/register-handler
 ::set-search-result
 (fn [db [_ search-result]]
   (assoc-in db [::search-result ] search-result)))

;; components
(defn- map-render []
  [:div.h-50
   ])

(defn- map-did-mount [this]
  (let [map-canvas (r/dom-node this)
        map-options (clj->js {"center" default-location
                              "zoom" 12})
        map-comp (js/google.maps.Map. map-canvas map-options)]
    (reset! map-ref map-comp)))

(defn- map-component []
  (r/create-class {:reagent-render map-render
                   :component-did-mount map-did-mount}))

(defn- markers []
  (let [items (rf/subscribe [::search-result ])]
    (fn []
      (let [new-markers
            (doall
             (map
              (fn [item]
                (let [geometry (goog.object/get item "geometry")
                      location (goog.object/get geometry "location")]
                  (js/google.maps.Marker. (clj->js {"position" location
                                                    "map" @map-ref}))))
              @items))]
        (rf/dispatch [::set-markers new-markers])))))

(defn- search []
  (let [search-text @(rf/subscribe [::search-text ])
        gmaps-searcher (js/google.maps.places.PlacesService. @map-ref)
        request (clj->js {"location" default-location
                          "radius" 500
                          "query" search-text})]
    (js-invoke gmaps-searcher "textSearch" request #(rf/dispatch [::set-search-result %]))))

(defn component []
  (fn []
    [:div.h-100.flex.flex-column
     [:div.h-50.flex.flex-column.justify-end
      [:div.bg-white.ph3 {:on-key-press #(when (= (goog.object/get % "charCode") 13)
                                           (search))}
       [ui/TextField {:id :map-search
                      :name "Map search"
                      :hintText "Venue name..."
                      :hintStyle {:color "rgba(0, 0, 0, 0.3)"}
                      :inputStyle {:color "rgba(0, 0, 0, 0.9)"}
                      :onChange (fn [event new-val]
                                  (rf/dispatch [::set-search-text new-val]))}]
       [:span.mh3 [ui/RaisedButton {:label "Search"
                                    :onClick #'search
                                    }]]]]
     [map-component ]
     [markers ]]))
