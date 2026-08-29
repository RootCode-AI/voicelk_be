# Code Owners

This file defines the code owners for the oicelk_be project. 
To use this with GitHub, you should also create a .github/CODEOWNERS file.

`	ext
# This is a comment.
# Each line is a file pattern followed by one or more owners.

# These owners will be the default owners for everything in the repo.
*       @thari

# Order is important; the last matching pattern takes the most precedence.

# Backend Java files
/src/main/java/**/*.java    @thari

# Build configuration
/pom.xml                    @thari
`

## Defining Owners
Owners can be defined using their GitHub/GitLab username (e.g., @username) or email address (e.g., user@example.com).
