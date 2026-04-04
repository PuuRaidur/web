import { API_BASE } from "../api/client";

function getInitials(name?: string | null) {
  if (!name) return "MM";
  return name.slice(0, 2).toUpperCase();
}

type AvatarProps = {
  name?: string | null;
  url?: string | null;
  size?: number;
  className?: string;
};

export default function Avatar({ name, url, size, className }: AvatarProps) {
  const displayUrl = url
    ? url.startsWith("/")
      ? `${API_BASE}${url}`
      : url
    : null;

  return (
    <div
      className={"avatar" + (className ? ` ${className}` : "")}
      style={size ? { width: size, height: size } : undefined}
    >
      {displayUrl ? (
        <img src={displayUrl} alt={name ?? "Profile"} />
      ) : (
        <span>{getInitials(name)}</span>
      )}
    </div>
  );
}
