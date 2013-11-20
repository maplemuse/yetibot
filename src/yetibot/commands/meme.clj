(ns yetibot.commands.meme
  (:require
    [taoensso.timbre :refer [info warn error]]
    [yetibot.hooks :refer [cmd-hook]]
    [yetibot.models.imgflip :as model]))


(defn- instance-result [json]
  (if (:success json)
    (-> json :data  :url)
    (str "Failed to generate:" (-> json :error_message))))

(defn generate-cmd
  "meme <generator>: <line1> / <line2> # generate an instance"
  [{[_ inst line1 line2] :match}]
  (instance-result
    (model/generate-meme-by-query inst line1 line2)))

(defn generate-auto-split-cmd
  "meme <generator>: <text> # autosplit <text> in half and generate the
   instance"
  [{[_ inst text] :match}]
  (instance-result
    (model/generate-meme-by-query inst text)))

(defn search-cmd
  "meme search <term> # query available meme generators"
  [{[_ term] :match}]
  (if-let [matches (model/search-memes term)]
    (map :name matches)
    (str "Couldn't find any memes for " term)))

(if model/configured?
  (cmd-hook ["meme" #"meme$"]
            ; #"^popular$" chat-instance-popular
            ; #"^popular\s(.+)" chat-instance-popular-for-gen
            ; #"^trending" trending-cmd
            #"^(.+?):(.+)\/(.*)$" generate-cmd
            #"^(.+?):(.+)$" generate-auto-split-cmd
            #"^(?:search\s)?(.+)" search-cmd)
  (info "Imgflip is not configured for meme generation"))
