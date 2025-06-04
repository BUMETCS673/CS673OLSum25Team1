import { useAuth } from "../contexts/AuthContext";

export default function Home() {
  const { user } = useAuth();
  return (
    <div>
      <h1>Home</h1>
      <p>Welcome {user.username} ({user.userEmail})</p>
    </div>
  );
}