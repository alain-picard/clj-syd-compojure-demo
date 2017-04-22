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

(defun ap-toggle-prezzo ()
  (interactive)
  (let ((routes (get-buffer "example_routes.clj"))
	(examples (get-buffer "examples.http"))
	(this (current-buffer)))
    (switch-to-buffer (if (eql this routes)
			  examples
			routes))
    (delete-other-windows)))



(define-key clojure-mode-map (kbd "C-x [") 'prev-hidden-page)
(define-key clojure-mode-map (kbd "C-x ]") 'next-hidden-page)
(define-key clojure-mode-map (kbd "<f5>") 'prev-hidden-page)
(define-key clojure-mode-map (kbd "<f6>") 'next-hidden-page)
(global-set-key (kbd "<f7>") 'ap-toggle-prezzo)
