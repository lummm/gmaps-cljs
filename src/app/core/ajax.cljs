(ns app.core.ajax
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [re-frame.core :as rf])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn rf-get [url request success-event]
  (go (let [response (<! (http/get url request))]
        (rf/dispatch [success-event response ]))))
