(ns mastodon.file-test
  (:require [clojure.test :refer :all]
            [org.satta.glob :as glob]
            [mastodon.file :as file]))

(deftest strip-path-test
  (is (= "foo.tar"
         (file/strip-path "/path/to/foo.tar"))))

(deftest jfile-name-test
  (is (= (file/jfile-name (clojure.java.io/file "/tmp/foo"))
         "foo")))

(deftest get-filenames-test
  (with-redefs [glob/glob (fn [path] '("/tmp/foo"))]
    (is (= '("foo")
           (file/get-filenames "/path/to/nowheresville/")))))

(deftest get-filenames-test-suffix
  (with-redefs [glob/glob (fn [path] '("/tmp/foo.tar" "/tmp/bar.tif"))]
    (is (= '("foo.tar")
           (file/get-filenames "/path/to/nowheresville/" "tar")))))
