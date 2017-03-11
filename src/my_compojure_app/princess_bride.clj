(ns my-compojure-app.princess-bride)


(defn make-mw [quote]
  (fn middleware-handler [h]
    (fn [request]
      (h (update request :quotes conj quote)))))

(defn render-quotes-to-html [request]
  (->> (:quotes request)
       (map (fn render [quote]
              [[:h1 "Quote"]
               [:p quote]]))))

(def inigo (make-mw "Hello.  My name is Inigo Montoya.  You killed my father.  Prepare to die."))

(def vizzini (make-mw "Inconceivable!"))

(def humperdinck  (make-mw "Iocane powder!"))

(def fezzik (make-mw "It's not my fault being the biggest and strongest: I don't even exercise!"))

(def man-in-black (make-mw "As you wish!"))

(def buttercup  (make-mw "Well... you were dead!"))

(def grandpa (make-mw "Isn't that a wonderful beginning?"))

(def kid (make-mw "Is this a kissing book?"))
