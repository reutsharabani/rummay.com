(ns rummy.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::selected-cards
 :selected-cards)

(re-frame/reg-sub
  ::effort
  :effort)
