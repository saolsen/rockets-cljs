(ns rockets.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as str]

            [rockets.physics :refer [apply-thrusters]]
            [rockets.scene :refer [scene-panal]]
            [rockets.rules :refer [control-panal]]))

;; use transit
;; look into immutable-js if you want to use just js
;; figure out component local state in the gamestate so I can make
;; tweak bar just tweak and stop needing to see gamestate all the time.

(enable-console-print!)

(def gamestate (atom {:frame {:then (.now js/Date)
                              :fps 0}
                      ;; can I just put a js object here?
                      :stats (let [s (js/Stats.)
                                   style (.-style (.-domElement s))]
                               ;;(.setMode s 1)
                               (aset style "position" "absolute")
                               (aset style "left" "0px")
                               (aset style "top" "0px")
                               (.appendChild (.-body js/document)
                                             (.-domElement s))
                               s)
                      :text "Rockets"
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
                              ;; (into {} (for [x (range 100)]
                              ;;            [x
                              ;;             {:text (str (int (* 10 (rand))))
                              ;;              :pos {:x (* 1000 (rand))
                              ;;                    :y (* 1000 (rand))}}]))
                              {:a {:text "A" :pos {:x 10 :y 10}}
                               :b {:text "B" :pos {:x 100 :y 200}}
                               :c {:text "C" :pos {:x 300 :y 150}}}
                              :edges
                              ;; (for [x (range 10)]
                              ;;   {:from (int (* 100 (rand)))
                              ;;    :to (int (* 100 (rand)))})
                              [{:from :a :to :b}
                               {:from :b :to :c}
                               {:from :a :to :c}]
                              }}))

(defn tweak-bar
  [node owner]
  (reify om/IRender
    (render [_]
      (dom/span nil
                ;; Figure out a good little tweak bar, for now show framerate
                "tweak bar"
                ))))

(defn update-game
  [state]
  (.begin (:stats state))
  (let [result
        (cond-> state
                ;; true (update-in [:frame] calc-framerate)

                ;; Hardcoded first entity to player ship (not ideal)
                (:running state) (update-in [:entities 0] apply-thrusters))]
    (.end (:stats state))
    result))

(defn game-tick []
  (.requestAnimationFrame js/window game-tick)
  (swap! gamestate update-game))

(game-tick)

(om/root
 (fn [app owner]
   (reify om/IRender
     (render [_]
       ;; We're gonna need some css up in here
       (dom/div nil
                ;;(om/build tweak-bar app)
                (om/build scene-panal app)
                (om/build control-panal app)
                )
       )))
 gamestate
 {:target (. js/document (getElementById "app"))})
