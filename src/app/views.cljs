(ns app.views
  (:require [re-frame.core :as re-frame]
            [app.components.nav :as nav]
            [app.pages.landing :as landing]
            [app.pages.about :as about]
            [reagent-material-ui.core :as ui]
            [app.core.material-ui :as mui]))

(defmulti pages identity)
(defmethod pages :landing [] [landing/page])
(defmethod pages :about [] [about/main])
(defmethod pages :default [] [:div.tc "Nothing here, chap."])

(defn main []
  (let [current-page (re-frame/subscribe [:current-page])]
    (fn []
      [ui/MuiThemeProvider mui/theme-defaults
       [:main
        [nav/navbar]
        (pages @current-page)]])))
