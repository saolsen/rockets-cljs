(ns rockets.scene
  "Rendering of the game scene."
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn round [n] (.round js/Math n))

;; Does rounding help?
(defn round4
  "Rounds n to 4 decimal places (I think that's enough)"
  [n]
  (/ (round (* n 1000)) 1000))
 
(defn get-transform
  "Takes the rocket and gets the transform string to be passed to the svg"
  [ship]
  (let [{:keys [position rotation]} ship
        [x y] position]
    (str "translate(" (round4 x) "," (round4 y) ") rotate(" rotation ")")))

(defn ship-body
  "Returns an svg element of the ship body"
  []
  (dom/g nil
   (dom/rect #js {:width 20 :height 40
                  :x -10 :y -25
                  :fill "red"})
   ;; Port booster
   (dom/rect #js {:width 15 :height 30
                  :x -20 :y -5
                  :fill "red"})
   ;; Starboard booster
   (dom/rect #js {:width 15 :height 30
                  :x 5 :y -5
                  :fill "red"})))

(defn ship-thrusters
  "Returns an svg element of the ships thrusters."
  [thrusters]
  (apply dom/g nil
         (remove nil?
                 [(when (:bp thrusters)
                    (dom/rect #js {:width 10 :height 10
                                   :x -20 :y -25
                                   :fill "yellow"}))
                  (when (:bs thrusters)
                    (dom/rect #js {:width 10 :height 10
                                   :x 10 :y -25
                                   :fill "yellow"}))
                  (when (:sp thrusters)
                    (dom/rect #js {:width 10 :height 10
                                   :x -30 :y 15
                                   :fill "yellow"}))
                  (when (:ss thrusters)
                    (dom/rect #js {:width 10 :height 10
                                   :x 20 :y 15
                                   :fill "yellow"}))
                  (when (:boost thrusters)
                    (dom/rect #js {:width 10 :height 10
                                   :x -17.5 :y 25
                                   :fill "yellow"}))
                  (when (:boost thrusters)
                    (dom/rect #js {:width 10 :height 10
                                   :x 7.5 :y 25
                                   :fill "yellow"}))])))

(defn display-ship
  [ship owner]
  (reify om/IRender
    (render [_]
      ;; The ship
      (dom/g #js {:transform (get-transform ship)}
             (ship-body)
             (ship-thrusters (:thrusters ship))
             ;; Center marker
             ;; (dom/rect #js {:width 1 :height 1
             ;;                :fill "black"})
             ))))

(defn scene-panal
  [app owner]
  (reify om/IRender
    (render [_]
      (dom/svg #js {:width "800" :height "600"
                    :backgroundColor "black"
                    :id "scene"}
               ;; Background
               (dom/rect #js {:width "100%"
                              :height "100%"
                              :fill "black"})
               ;; Ship
               (om/build display-ship (first (:entities app)))
               ))))

