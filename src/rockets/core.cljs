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
                                   :boost false}}]}))

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
       (om/build display-scene app))))
 gamestate
 {:target (. js/document (getElementById "app"))})
