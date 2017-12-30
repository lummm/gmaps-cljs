(ns app.components.nav
  (:require [app.routes :as routes]))

(defn nav-item [text]
  [:span.flex.flex-column.justify-around.h-100
   [:a.link.white-70.hover-white.flex.items-center.pa3
       text]])

(defn main []
  (fn []
    [:nav.flex.justify-between.bb.bg-black.h3.absolute.w-100.br2
     [nav-item "Home"]
     [nav-item "Login"]
     ]))
