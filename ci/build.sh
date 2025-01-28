#! /bin/bash

function logAndExec() {
  echo 1>&2 "Executing:" "${@}"
  exec "${@}"
}

logAndExec ./gradlew check "${@}" -Plog-test-progress=true --stacktrace
