(ns rockets.rules
  "Rockets rules system"
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

;; TODO(stephen): add a compile time check that all of these are account for in all
;; my multimethods so when I add a new one I know everywhere to update it.
(def kinds #{:root :signal :constant :predicate :gate :thruster})
;; possible future kinds, stack, var, label

(def test-rules
  {:nodes [{:kind :root
            :id :root :x 0 :y 14}
           {:input :root
            :id :booster
            :kind :thruster
            :thruster :booster
            :x 0 :y 34}]})

;; todo(stephen): maybe store the rules as a map from id, so linking them up is easier?
;; I guess linking up will be done by clicking and the html element will know it's id
;; so maybe it won't be necessary but keep it in mind.
(def slightly-more-complicated-test-rules
  {:nodes [{:kind :root
            :id :root
            :x 100 :y 14}
           {:kind :predicate
            :id :pred
            :x 100 :y 24
            :lhs :lh
            :rhs :rh
            :op :less-than}
           {:kind :signal
            :id :lh
            :x 0 :y 24
            :signal :pos-x
            :input :root}
           {:kind :constant
            :id :rh
            :x 200 :y 24
            :val 100}
           {:kind :thruster
            :id :booster
            :y 44 :x 200
            :thruster :booster
            :input :pred
            }]})

;; Eval
;; (defn build-tree
;;   "Builds the processing tree from the list of nodes.
;;   We keep the nodes in a list because it's easier to
;;   display them and work with them but we need a tree
;;   from the root for processing."
;;   [nodes]
;;   (let [nodes-by-id (into {} (map #([(:id %) %]) nodes))
;;         empty-children-map (into {} (map #([(:id %) []]) nodes))

;;         children-map (reduce #(update-in %1 [(:id %2) ]) empty-children-map nodes)
;;         root (:root nodes-by-id)
;;         ]
    

;;     )
;; )

(defn eval-rules
  [nodes]
  (let [thrusters (filter :thruster nodes)]
    (println thrusters)))



;; Display
(defmulti node-element :kind)

(defmethod node-element :root
  [node]
  (dom/text #js {:className "root-node" :y (:y node) :x (:x node)}
            "POWER"))

(defmethod node-element :thruster
  [node]
  (dom/text #js {:className "thruster" :y (:y node) :x (:x node)}
            (str (:thruster node))))

(defmethod node-element :predicate
  [node]
  (dom/text #js {:className "predicate" :y (:y node) :x (:x node)}
            (str (:op node))))

(defmethod node-element :signal
  [node]
  (dom/text #js {:className "signal" :y (:y node) :x (:x node)}
            (str (:signal node))))

(defmethod node-element :constant
  [node]
  (dom/text #js {:className "constant" :y (:y node) :x (:x node)}
            (str (:val node))))

(defn display-node
  [node owner]
  (reify om/IRender
    (render [_]
      (node-element node))))

(defn control-panal
  [app owner]
  (reify om/IRender
    (render [_]
      (apply dom/svg #js {:width "338" :height "700" :id "controls"}
             (om/build-all display-node (:nodes (:rules app)))))))
