package erp.infrastructure

final case class AppConfig(
    port: Int,
    supabaseUrl: String,
    supabaseJwtSecret: String,
    databaseUrl: String
)

object AppConfig:
  def load: AppConfig =
    AppConfig(
      port = sys.env.get("PORT").flatMap(_.toIntOption).getOrElse(8080),
      supabaseUrl = sys.env.getOrElse("SUPABASE_URL", ""),
      supabaseJwtSecret = sys.env.getOrElse("SUPABASE_JWT_SECRET", ""),
      databaseUrl = sys.env.getOrElse("DATABASE_URL", "")
    )
