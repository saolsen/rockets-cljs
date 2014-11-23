(ns rockets.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string :as str]

            [rockets.physics :refer [apply-thrusters]]
            [rockets.scene :refer [scene-panal]]
            [rockets.rules :refer [control-panal test-rules]]))

;; use transit
;; look into immutable-js if you want to use just js
;; figure out component local state in the gamestate so I can make
;; tweak bar just tweak and stop needing to see gamestate all the time.
;; Need to understand transducers

(enable-console-print!)

(def level1 {:ship {:position [300 20]
                    :rotation 180
                    :thrusters
                    {:bp false :bs false
                     :sp false :ss false
                     :boost true}}
             :goal {:position [300 650]
                    :rotation 0}})

(def gamestate (atom {:stats (let [s (js/Stats.)
                                   style (.-style (.-domElement s))]
                               ;;(.setMode s 1)
                               (aset style "position" "absolute")
                               (aset style "left" "0px")
                               (aset style "top" "0px")
                               (.appendChild (.-body js/document)
                                             (.-domElement s))
                               s)
                      :running true
                      :scene level1
                      :rules test-rules}))

(defn check-if-winner
  [state]
  (let [scene (:scene state)]
    (if (= (:position (:ship scene))
           (:position (:goal scene)))
      (do
        (js/alert "YOU WIN GOOD JOB YOU!")
        (assoc state :running false))
      state)))

(defn update-game
  [state]
  (.begin (:stats state))
  (let [result
        (cond-> state
                (:running state) (update-in [:scene :ship] apply-thrusters)
                (:running state) check-if-winner
                )]
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
