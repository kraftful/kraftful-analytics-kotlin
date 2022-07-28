Releasing
=========

Run `./release RELEASE_VERSION NEXT_VERSION`.
Once the script pushes the changes to GH create a PR and merge it. This will trigger a CI job that pushes a stable release to Maven Centra.

Example: `./release 0.0.2 0.0.3`.
