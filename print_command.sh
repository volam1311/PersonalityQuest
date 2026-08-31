# This command prints commit history to file commit.txt
git log --no-merges --pretty=format:'%h was %an (%ae), %at, %ar, message: %s' > commit.txt 