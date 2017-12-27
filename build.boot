(set-env!
 :source-paths    #{"src"}
 :resource-paths  #{"resources"}
 :dependencies '[[adzerk/boot-cljs "2.1.4"]
                 [adzerk/boot-cljs-repl         "0.3.3"]
                 [adzerk/boot-reload            "0.5.2"]
                 ;; [pandeiro/boot-http            "0.7.3"           :scope "test"]
                 [com.cemerick/piggieback        "0.2.1"]
                 ;; [crisptrutski/boot-cljs-test   "0.2.2-SNAPSHOT"  :scope "test"]
                 [weasel                        "0.7.0"]
                 [org.clojure/tools.nrepl       "0.2.13"]
                 [binaryage/devtools            "0.9.8"]
                 ;; end what was previously scoped 'test'
                 [org.clojure/clojurescript     "1.9.946"]
                 [reagent                       "0.8.0-alpha2"]
                 [re-frame                      "0.10.3-beta1"]
                 [bidi                          "2.1.2"]
                 [kibu/pushy                    "0.3.8"]])

(require
 '[adzerk.boot-cljs               :refer [cljs]]
 '[adzerk.boot-cljs-repl          :refer [cljs-repl cljs-repl-env]]
 '[adzerk.boot-reload             :refer [reload]]
 ;; '[crisptrutski.boot-cljs-test    :refer [test-cljs]]
 )

(deftask dev []
  (comp (watch)
        (cljs-repl-env)
        (cljs-repl)
        (reload :on-jsload 'app.core/mount-root)
        ;; (serve :dir "target" :not-found 'dev.not-found/not-found-handler :port 8080)
        (cljs :compiler-options {:preloads '[devtools.preload]})
        (target)))
