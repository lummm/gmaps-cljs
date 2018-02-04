(ns app.gmaps.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [app.core.ajax :as ajax]
            [reagent-material-ui.core :as ui])
  (:require-macros [reagent.ratom :refer [reaction]]))

(def gmaps-api-key "AIzaSyBUgRXwRbFEK-UC0aRjJg1vydO2rVhYz3Q")

(def default-location (js/google.maps.LatLng. 49.28, -123.09))
(def map-ref (atom {}))
(def max-photo-dimensions
  (clj->js {"maxHeight" 500
            "maxWidth" 1000}))

(defn make-info-display [marker]
  (let [current-results @(rf/subscribe [::search-result ])
        relevant-result (first
                         (filter
                          #(-> %
                               (goog.object/get "geometry")
                               (goog.object/get "location")
                               (= (goog.object/get marker "position")))
                          current-results))]
    (str
     "<div>"
     "  <h1>" (goog.object/get relevant-result "name") "</h1>"
     "  <p>" (goog.object/get relevant-result "formatted_address") "</p>"
     "  <img style='height: 300px;' src='" (goog.object/get relevant-result "summary-photo") "' />"
     "</div>")))

;; db
(def default-db
  {::search-text nil
   ::search-result nil
   ::markers nil
   ::search-suggestions nil
   ::location nil})

;; subs
(rf/reg-sub-raw
 ::location
 (fn [db]
   (reaction (-> @db ::location))))

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
 ::set-location
 (fn [db [_ coords]]
   (assoc-in db [::location ] coords)))

(rf/register-handler
 ::set-search-text
 (fn [db [_ search-text]]
   (assoc-in db [::search-text ] search-text)))

(rf/register-handler
 ::set-search-suggestions
 (fn [db [_ search-suggestions]]
   (assoc-in db [::search-suggestions ] search-suggestions)))

(rf/register-handler
 ::set-search-result
 (fn [db [_ search-result]]
   (assoc-in db [::search-result ] search-result)))

;; components
(defn- map-render []
  [:div.h-100
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

(defn- fetch-search-suggestions []
  (let [search-text @(rf/subscribe [::search-text ])]
    ))

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
    (js-invoke
     gmaps-searcher "textSearch" request
     (fn [response]
       (let [response-with-photos
             (into
              []
              (map
               (fn [response-item]
                 (-> response-item
                     (js->clj )
                     (merge
                      {"summary-photo"
                       (let [photos (goog.object/get response-item "photos")]
                         (if (empty? photos) nil
                             (js-invoke (nth photos 0) "getUrl" max-photo-dimensions)))})
                     (clj->js )))
               response))]
         (rf/dispatch [::set-search-result response-with-photos]))))))

(defn search-bar
  []
  (let []
     (r/create-class
      {:component-did-mount
       #(let [input-element (js-invoke js/document "getElementById" "map-search")
              delta 10
              lat 49.281561
              lng -123.105271
              sw (google.maps.LatLng. (- lat delta) (- lng delta))
              ne (google.maps.LatLng. (+ lat delta) (+ lng delta))
              bounds (google.maps.LatLngBounds. sw ne)
              autocomplete (js/google.maps.places.Autocomplete.
                            input-element (clj->js {"strictBounds" false
                                                    "bounds" bounds}))]
          (js-invoke js/google.maps.event "addListener" autocomplete "place_changed"
                     (fn []
                       (let [selected-place (js-invoke autocomplete "getPlace")
                             location (-> selected-place
                                          (goog.object/get "geometry")
                                          (goog.object/get "location"))]
                         (rf/dispatch [::set-search-text (goog.object/get selected-place "name")])))))
       :display-name  "search-bar"
       :reagent-render
       (fn []
         [:div.bg-white.ph3.flex {:on-key-press #(when (= (goog.object/get % "charCode") 13)
                                              (search))}
          [:div.w-50
           [ui/TextField {:id :map-search
                          :name "Map search"
                          :hintText ""
                          :hintStyle {:color "rgba(0, 0, 0, 0.3)"}
                          :inputStyle {:color "rgba(0, 0, 0, 0.9)"}
                          :fullWidth true
                          :onChange (fn [event new-val]
                                      (rf/dispatch [::set-search-text new-val]))}]]
          [:div.w-50.flex.items-center
           [:span.mh3 [ui/RaisedButton {:label "Search"
                                        :onClick #'search
                                        }]]]])
       })))

(defn- location-prompt []
  (js-invoke
   (goog.object.get js/navigator "geolocation") "getCurrentPosition"
   (fn [pos]
     (rf/dispatch [::set-location
                   (let [coords (goog.object.get pos "coords")]
                     {:latitude (goog.object.get coords "latitude")
                      :longitude (goog.object.get coords "longitude")})]))))

(defn component []
  (location-prompt)
  (fn []
    [:div.h-100.flex.flex-column
     [search-bar ]
     [map-component ]
     [markers ]]))
