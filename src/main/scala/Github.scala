case class User(
                 id: String,
                 login: String,
                 gravatar_id: String,
                 url: String,
                 avatar_url: String
               )

case class Repo(
                 id: String,
                 name: String,
                 url: String
               )

case class Activity(
                     id: String,
                     `type`: String,
                     actor: User,
                     repo: Repo,
                     created_at: String,
                     org: User
                   )
