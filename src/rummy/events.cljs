(ns rummy.events
  (:require
   [re-frame.core :as re-frame]
   [rummy.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))


(re-frame/reg-event-db
  ::select-card
  (fn [db [_ rank suit]]
    (update db :selected-cards conj {:rank rank :suit suit})))

(re-frame/reg-event-db
  ::clear-selection
  (fn [db [_ rank suit]]
    (assoc db :selected-cards [])))

(re-frame/reg-event-db
  ::remove-card
  (fn [db [_ rank suit]]
    (let [old-cards (:selected-cards db)]
      (assoc db :selected-cards (remove #(= % {:suit suit :rank rank}) old-cards)))))

(re-frame/reg-event-db
  ::set-effort
  (fn [db [_ effort]]
    (assoc db :effort effort)))
