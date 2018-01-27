(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs              "2.1.4"]
                 [adzerk/boot-cljs-repl         "0.3.3"]
                 [adzerk/boot-reload            "0.5.2"]
                 [com.cemerick/piggieback       "0.2.1"]
                 [cljs-http                     "0.1.44"]
                 [weasel                        "0.7.0"]
                 [org.clojure/tools.nrepl       "0.2.13"]
                 [binaryage/devtools            "0.9.8"]
                 [org.clojure/clojurescript     "1.9.946"]
                 [reagent                       "0.8.0-alpha2"]
                 [re-frame                      "0.10.3-beta1"]
                 [bidi                          "2.1.2"]
                 [reagent-material-ui           "0.2.5"]
                 [kibu/pushy                    "0.3.8"]])

(require
 '[adzerk.boot-cljs               :refer [cljs]]
 '[adzerk.boot-cljs-repl          :refer [cljs-repl cljs-repl-env]]
 '[adzerk.boot-reload             :refer [reload]]
 )

(def base-compile-opts
  {:externs ["externs.js"]})

(deftask dev []
  (comp (watch)
        (cljs-repl-env)
        (cljs-repl)
        (reload :on-jsload 'app.core/mount-root)
        (cljs :compiler-options (merge
                                 base-compile-opts
                                 {:preloads '[devtools.preload]}))
        (target)))

(deftask prod []
  (comp (cljs :compiler-options (merge
                                 base-compile-opts
                                 {:optimizations :advanced}))
        (target)))
