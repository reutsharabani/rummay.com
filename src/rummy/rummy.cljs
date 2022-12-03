(ns rummy.rummy)

(defn rank-run [serie]
  (let [min-card (apply min-key :rank serie)
        max-card (apply max-key :rank serie)
        suit (:suit (first serie))
        rank-run-with-ace (when (= (:rank min-card) 1)
                            (rank-run (-> serie
                                          (disj min-card)
                                          (conj (assoc min-card
                                                       :rank 14)))))]
    (or
      rank-run-with-ace
      (and
        (= (count (set serie)) (count serie))
        (= (set serie) (set (for [rank (range (:rank min-card) (inc (:rank max-card)))]
                              {:suit suit
                               :rank rank})))))))

(defn suit-run [serie]
  (let [distinct-suits (count (set (map :suit serie)))
        distinct-ranks (count (set (map :rank serie)))]
    (and
      (= 1 distinct-ranks)
      (= (count serie) distinct-suits))))

(defn valid? [serie]
  (or (rank-run serie)
      (suit-run serie)))

(defn complete? [serie]
  (and (<= 3 (count serie))
       (valid? serie)))

(defn can-add? [card serie]
  (and (not (serie card))
       (or (= #{(:rank card)} (set (map :rank serie)))
           (= #{(:suit card)} (set (map :suit serie))))))

(defn remove-serie [solution serie]
  (let [[before after] (split-with (partial not= serie) solution)]
    (into [] cat [before (rest after)])))

(defn spawn-solutions [card solution]
  (let [candidates (filter (partial can-add? card) solution)]
    (into #{(conj solution #{card})}
          (for [candidate candidates]
            (-> solution
                (remove-serie candidate)
                (conj (conj candidate card))
                (->> (into [])))))))

(defn rank-solution [solution]
  (* -1 (count (into [] cat (filter complete? solution)))))

(defn best [solutions]
  (when (seq solutions)
    (apply min-key rank-solution solutions)))

(defn solve
  [cards]
  (let [cards-left (atom (->> cards
                              (sort-by :suit)
                              (sort-by :rank)))
        solutions (atom #{[]})
        winner (atom nil)]
    (while (and (not @winner) (seq @cards-left))
      (let [[card & rest-of-cards] @cards-left
            new-solutions (into #{} (mapcat (partial spawn-solutions card)
                                            @solutions))]
        (reset! cards-left rest-of-cards)
        (reset! solutions (->> new-solutions
                               (sort-by rank-solution)
                               (take 100)))
        (when (empty? rest-of-cards)
          (let [w (best new-solutions)]
            (.log js/console (str w))
            (reset! winner w)))))
    (group-by complete? @winner)))
