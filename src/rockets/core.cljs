(ns rockets.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as str]

            [rockets.physics :refer [apply-thrusters]]
            [rockets.scene :refer [display-scene]]))

(enable-console-print!)

(def gamestate (atom {:text "Rockets"
                      :running true
                      :entities [{:id :player-ship
                                  :position [100 100]
                                  :color :red
                                  :rotation 90
                                  :thrusters
                                  {:bp true :bs true
                                   :sp true :ss false
                                   :boost false}}]
                      :controls [{:text "Pos X"}
                                 {:text "Pos Y"}
                                 {:text "Rotation"}
                                 {:text "hi jeremy"}]
                      :rules {:nodes
                              ;; {:a {:text "A" :pos {:x 10 :y 10}}
                              ;;  :b {:text "B" :pos {:x 100 :y 100}}
                              ;;  :c {:text "C" :pos {:x 300 :y 300}}}
                              (into {} (for [x (range 100)]
                                         [x
                                          {:text (str (int (* 10 (rand))))
                                           :pos {:x (* 1000 (rand))
                                                 :y (* 1000 (rand))}}]))
                              :edges (for [x (range 10)]
                                       {:from (int (* 100 (rand)))
                                        :to (int (* 100 (rand)))})
                              ;; [{:from :a :to :b}
                              ;;  {:from :b :to :c}
                              ;;  {:from :a :to :c}]
                              }}))

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

(defn controls
  [app owner]
  (reify om/IRender
    (render [_]
      (apply dom/svg #js {:width "100%" :height "100%"}           
             (om/build edges (:rules app))
             (om/build-all node (vals (:nodes (:rules app))))))))
;;;;;;;;

(defn update-game
  [state]
  (if (:running state)
    ;; Hardcoded first entity to player ship (not ideal)
    (update-in state [:entities 0] apply-thrusters)
    state))

(defn game-tick []
  (.requestAnimationFrame js/window game-tick)
  (swap! gamestate update-game))

;; Entry Points
(game-tick)

(om/root
 (fn [app owner]
   (reify om/IRender
     (render [_]
       ;;(om/build display-scene app)
       (om/build controls app)
       )))
 gamestate
 {:target (. js/document (getElementById "app"))})

;; (om/root
;;  (fn [app owner]
;;    (reify om/IRender
;;      (render [_]
;;        (om/build controls app))))
 gamestate
 { :target (. js/document (getElementById "game-controls"))})
