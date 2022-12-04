(ns rummy.views
  (:require
   [re-frame.core :as re-frame]
   [rummy.rummy :as rummy]
   [rummy.subs :as subs]
   [rummy.events :as events]))

(def suit->suit-name
  {:h "♥"
   :d "♦"
   :c "♣"
   :s "♠"})

(def rank->rank-name
  {1 1
   2 2
   3 3
   4 4
   5 5
   6 6
   7 7
   8 8
   9 9
   10 10
   11 "J"
   12 "Q"
   13 "K"})

(def suit->color
  {:h "red"
   :d "red"
   :c "black"
   :s "black"})

(defn card [& {:keys [rank suit]}]
  [:p {:style {:padding-left "10px"
               :color (suit->color suit)}
       :on-click #(re-frame/dispatch [::events/select-card rank suit])} (rank->rank-name rank) (suit->suit-name suit)])

(defn solution-card [& {:keys [rank suit]}]
  [:p {:style {:padding-left "10px"
               :color (suit->color suit)}
       :on-click #(re-frame/dispatch [::events/remove-card rank suit])} (rank->rank-name rank) (suit->suit-name suit)])

(defn card-picker []
  (into [:div [:h1 "select cards here"]]
        (into []
              (for [suit [:d :h :c :s]]
                ^{:key suit} [:div {:style {:display "flex"}}
                              [:p {:style {:color (suit->color suit)}} (suit->suit-name suit)]
                              (for [rank (range 13)]
                                ^{:key [rank suit]}
                                [card {:rank (inc rank) :suit suit}])]))))

(defn effort-picker []
  (let [effort (re-frame/subscribe [::subs/effort])]
    [:div
     [:label "effort"
      [:input {:type "number"
               :min 100
               :max 3000
               :step 50
               :value @effort
               :on-change (fn [e]
                            (let [effort (-> e .-target .-value js/parseInt)]
                              (.log js/console effort)
                              (re-frame/dispatch [::events/set-effort effort])))}]]]))

(defn solution []
  (let [selected-cards (re-frame/subscribe [::subs/selected-cards])
        {valid true
         invalid false} (rummy/solve @selected-cards :effort (:effort (deref re-frame.db/app-db)) )]
    [:div {:style {:display "flex"}}
     (into [:div {:style {:background-color "lightgreen"
                          :width "50%"}} [:p "valid"]]
           (for [serie valid]
             (into [:div {:style {:display "flex"}}]
                   (->> serie
                        (sort-by :rank)
                        (sort-by :suit)
                        (map solution-card)))))
     [:div {:style {:background-color "lightsalmon"
                    :width "50%"}} [:p "invalid"]
      (into [:div {:style {:display "flex"}}]
            (->> invalid
                 (into [] cat)
                 (sort-by :rank)
                 (sort-by :suit)
                 (map solution-card)))]]))

(defn reset-button []
  [:input {:type "button"
           :value "reset"
           :on-click #(re-frame/dispatch [::events/clear-selection])}])

(defn main-panel []
  [:div
   [card-picker]
   [reset-button]
   [effort-picker]
   [solution]])
