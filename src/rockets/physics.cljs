(ns rockets.physics)

(def screen-width 600)
(def screen-height 700)

(def cos js/Math.cos)
(def sin js/Math.sin)
(def round js/Math.round)
(def pi js/Math.PI)

(defn to-radians [rotation]
  (* (/ pi 180) rotation))

(defn calc-relative-force
  [{:keys [bp bs sp ss boost]}]
  (let [r 0.5]
    (cond->> [0 0 0]
             bp    (mapv + [r 2 0])
             bs    (mapv + [(- r) -2 0])
             sp    (mapv + [(- r) 2 0])
             ss    (mapv + [r -2 0])
             boost (mapv + [0 0 -5])
             )))

(defn gamespace-force
  [rotation [x y]]
  (let [rot (to-radians rotation)]
    (mapv (fn [n] (/ (round (* 100 n)) 100))
          [(- (* x (cos rot)) (* y (sin rot)))
           (+ (* x (sin rot)) (* y (cos rot)))])))

(defn apply-thrusters
  [{:keys [position rotation thrusters] :as s}]
  (let [relative-force (calc-relative-force thrusters)
        global-force (gamespace-force rotation (rest relative-force))
        new-rotation (mod (+ rotation (first relative-force)) 360)
        [x y :as new-position] (mapv + global-force position)
        collision-position [(cond
                             (< x 0) 0
                             (> x screen-width) screen-width
                             :else x)
                            (cond
                             (< y 0) 0
                             (> y screen-height) screen-height
                             :else y)]]
    (assoc s
      :rotation new-rotation
      :position collision-position)))
