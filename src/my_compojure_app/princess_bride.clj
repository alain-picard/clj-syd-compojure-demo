(ns my-compojure-app.princess-bride
  (:require [hiccup.core :refer [html]]))

(defn make-mw [quote]
  (fn middleware-handler [h role]
    (fn [request]
      (h (update request
                 :quotes
                 conj [role quote])))))

 (defn render-quotes-in-request [request]
  [:div
   (for [[role quote] (:quotes request)]
     [:div role " says: " [:strong quote] [:hr]])])


(def inigo (make-mw "Hello.  My name is Inigo Montoya.  You killed my father.  Prepare to die."))

(def vizzini (make-mw "Inconceivable!"))

(def humperdinck  (make-mw "Iocane powder!"))

(def fezzik (make-mw "It's not my fault being the biggest and strongest: I don't even exercise!"))

(def man-in-black (make-mw "As you wish!"))

(def buttercup  (make-mw "Well... you were dead!"))

(def grandpa (make-mw "Isn't that a wonderful beginning?"))

(def kid (make-mw "Is this a kissing book?"))
