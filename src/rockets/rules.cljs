(ns rockets.rules
  "Rockets rules system"
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn node
  [node owner]
  (reify om/IRender
    (render [_]
      (dom/text #js {:width 10 :height 10
                     :x (:x (:pos node)) :y (:y (:pos node))
                     :fill "blue"} (:text node)))))

(defn edges
  [rules owner]
  (reify om/IRender
    (render [_]
      (let [edges (:edges rules)
            nodes (:nodes rules)
            mapper (fn [edge]
                     (let [from (get nodes (:from edge))
                           to (get nodes (:to edge))]
                       (assoc edge
                         :fromx (:x (:pos from))
                         :fromy (:y (:pos from))
                         :tox (:x (:pos to))
                         :toy (:y (:pos to)))))
            with-coordinates (map mapper edges)]
        (apply dom/g nil
               (map #(dom/line #js {:x1 (:fromx %) :y1 (:fromy %)
                                    :x2 (:tox %) :y2 (:toy %)
                                    :stroke "red"})
                    with-coordinates))))))

(defn control-panal
  [app owner]
  (reify om/IRender
    (render [_]
      (apply dom/svg #js {:width "359" :height "700" :id "controls"}
             (om/build edges (:rules app))
             (om/build-all node (vals (:nodes (:rules app))))))))
