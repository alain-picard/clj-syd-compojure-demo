(defun next-hidden-page ()
  (interactive)
  (widen)
  (forward-page)
  (narrow-to-page))

(defun prev-hidden-page ()
  (interactive)
  (widen)
  (backward-page 2)
  (narrow-to-page))



(define-key clojure-mode-map (kbd "C-x [") 'prev-hidden-page)
(define-key clojure-mode-map (kbd "C-x ]") 'next-hidden-page)
