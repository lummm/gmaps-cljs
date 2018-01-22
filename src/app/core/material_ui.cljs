(ns app.core.material-ui
  (:require [reagent-material-ui.core :as ui]
            [reagent.core :as r]))

(defn color [nme] (aget ui/colors nme))
(defn icon [nme] [ui/FontIcon {:className "material-icons"} nme])

;; create a new theme based on the dark theme from Material UI
(defonce theme-defaults
  {:muiTheme (ui/getMuiTheme
              (-> ui/darkBaseTheme
                  (js->clj :keywordize-keys true)
                  (update :palette merge {:primary1Color (color "green500")
                                          :primary2Color (color "amber700")})
                  clj->js))})
