(ns conduit.controllers.articles)

(def initial-state
  {:articles []
   :page 0
   :pages-count 0
   :loading? false})

(defmulti control (fn [event] event))

(defmethod control :default [_ _ state]
  {:state state})

(defmethod control :init []
  {:state initial-state})


(defn page->offset [page]
  (* 20 (- page 1)))

(defn ->params [{:keys [page]}]
  (when (not= page nil)
    {:offset (page->offset page)}))

(defmethod control :load [_ [{:keys [page]}] state]
  (js/console.log "controller" page)
  {:state (assoc state :loading? true)
   :http {:endpoint :articles
          :params (->params {:page page})
          :on-load [:load-ready page]}})

(defmethod control :load-ready [action [{:keys [articles articlesCount]}] state]
  (js/console.log "load ready" action)
  {:state
   (-> state
       (assoc :articles articles)
       (assoc :page 1)
       (assoc :pages-count (-> articlesCount (/ 10) Math/round))
       (assoc :loading? false))})
