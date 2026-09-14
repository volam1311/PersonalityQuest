# This command prints commit history to file commit.txt
git log --pretty=format:'%h was %an (%ae), %at, %ar, message: %s' > commits.txt 
git shortlog --summary --numbered --all --no-merges > shortlog.txt