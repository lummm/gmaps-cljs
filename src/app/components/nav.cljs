(ns app.components.nav
  (:require [app.routes :as routes]
            [app.core.material-ui :as mui]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [reagent-material-ui.core :as ui])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn default-db []
  {::menu-open false})

(rf/reg-sub-raw
 ::menu-open
 (fn [db]
   (reaction (-> @db ::menu-open))))

(rf/register-handler
 ::menu-open
 (fn [db [_ is-open]]
   (assoc-in db [::menu-open ] is-open)))

(defn navbar []
  (let [is-menu-open (rf/subscribe [::menu-open])]
    (fn []
      [:div
       [ui/AppBar {:title "Title"
                   :onLeftIconButtonTouchTap #(rf/dispatch [::menu-open true ])}]
       [ui/Drawer {:open @is-menu-open
                   :docked false
                   :onRequestChange #(rf/dispatch [::menu-open false])}
        [ui/List
         (r/as-element
          [:a {:href "/"
               :on-click #(rf/dispatch [::menu-open false])}
           [ui/ListItem {:leftIcon (r/as-element [:i.material-icons "home"])} "Home"]])
         [ui/Divider]]
        ]])))
