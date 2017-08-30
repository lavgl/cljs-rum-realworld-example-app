(ns conduit.core
  (:require [rum.core :as rum]
            [citrus.core :as citrus]
            [bidi.verbose :as r]
            [goog.dom :as dom]
            [conduit.effects :as effects]
            [conduit.router :as router]
            [conduit.controllers.articles :as articles]
            [conduit.controllers.tags :as tags]
            [conduit.controllers.tag-articles :as tag-articles]
            [conduit.controllers.article :as article]
            [conduit.controllers.comments :as comments]
            [conduit.controllers.router :as router-controller]
            [conduit.components.root :refer [Root]]
            [conduit.components.home :as home]
            [conduit.components.article :refer [Article]]))

;; (def routes
;;   ["/" [["" ["" :home]
;;          [["/" :page] :home]]
;;         [["tag/" :id] [["" :tag]
;;                        [["/" :page] :tag]]]
;;         [["article/" :id] :article]]])

(def routes
  (r/branch "/"
            (r/branch ""
                      (r/branch "" (r/param :page) (r/leaf "" :home))
                      (r/leaf "" :home))
            (r/branch "tag/" (r/param :id) (r/leaf "" :tag))
            (r/branch "article/" (r/param :id) (r/leaf "" :article))))


;; create Reconciler instance
(defonce reconciler
  (citrus/reconciler
    {:state (atom {})
     :controllers
     {:articles articles/control
      :tag-articles tag-articles/control
      :tags tags/control
      :article article/control
      :comments comments/control
      :router router-controller/control}
     :effect-handlers {:http effects/http}}))

;; initialize controllers
(defonce init-ctrl (citrus/broadcast-sync! reconciler :init))

(router/start! #(citrus/dispatch! reconciler :router :push %) routes)

(rum/mount (Root reconciler)
           (dom/getElement "app"))

