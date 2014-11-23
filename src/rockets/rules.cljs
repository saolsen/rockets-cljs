(ns rockets.rules
  "Rockets rules system"
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

;; Kind? [:root :signal :constant :predicate :gate :thruster]
;; possible future kinds, stack, var, label

(def test-rules
  {:kind :root
   :children [{:kind :thruster :thruster :booster}]})

(def slightly-more-complicated-test-rules
  {:kind :root
   :children [{:kind :predicate
               :lhs {:kind :signal :signal :pos-y}
               :rhs {:kind :constant :val 100}
               :op :less-than
               :children [{:kind :thruster :thruster :booster}]}]})

(defmulti node-element :kind)

(defmethod node-element :root
  [node]
  (dom/a #js {:className "root-node"}
         "POWER"))

(defmethod node-element :thruster
  [node]
  (dom/a #js {:className "thruster"}
         (str (:thruster node))))

(defmethod node-element :predicate
  [node]
  (apply dom/div #js {:className "predicate-div"}
         (node-element (:lhs node))
         (dom/a #js {:className "predicate"} (:op node))
         (node-element (:rhs node))))

(defmethod node-element :signal
  [node]
  (dom/a #js {:className "signal"}
         (str (:signal node))))

(defmethod node-element :constant
  [node]
  (dom/a #js {:className "constant"}
         (str (:val node))))

(defn display-node
  [node owner]
  (reify om/IRender
    (render [_]
      (node-element node))))

;; Gonna try something sorta silly first.
(defn control-panal
  [app owner]
  (reify om/IRender
    (render [_]
      (dom/svg #js {:width "338" :height "708" :id "controls"}

                    (dom/rect #js {:width "100%"
                                   :height "100%"
                                   :fill "blue"})

                    ))))

;; (defn node
;;   [node owner]
;;   (reify om/IRender
;;     (render [_]
;;       (dom/text #js {:width 10 :height 10
;;                      :x (:x (:pos node)) :y (:y (:pos node))
;;                      :fill "blue"} (:text node)))))

;; (defn edges
;;   [rules owner]
;;   (reify om/IRender
;;     (render [_]
;;       (let [edges (:edges rules)
;;             nodes (:nodes rules)
;;             mapper (fn [edge]
;;                      (let [from (get nodes (:from edge))
;;                            to (get nodes (:to edge))]
;;                        (assoc edge
;;                          :fromx (:x (:pos from))
;;                          :fromy (:y (:pos from))
;;                          :tox (:x (:pos to))
;;                          :toy (:y (:pos to)))))
;;             with-coordinates (map mapper edges)]
;;         (apply dom/g nil
;;                (map #(dom/line #js {:x1 (:fromx %) :y1 (:fromy %)
;;                                     :x2 (:tox %) :y2 (:toy %)
;;                                     :stroke "red"})
;;                     with-coordinates))))))

;; (defn control-panal
;;   [app owner]
;;   (reify om/IRender
;;     (render [_]
;;       (apply dom/svg #js {:width "338" :height "708" :id "controls"}
;;              (om/build edges (:rules app))
;;              (om/build-all node (vals (:nodes (:rules app))))))))

