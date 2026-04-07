import { Outlet } from "react-router-dom";
import { useEffect, useState } from "react";
import SideNav from "../components/SideNav";
import { fetchChats } from "../api/client";
import { addChatListener, connectChatSocket } from "../realtime/chatSocket";

export default function AppLayout() {
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    let isActive = true;

    async function refreshUnread() {
      try {
        const chats = await fetchChats();
        if (!isActive) return;
        const total = chats.reduce((sum, chat) => sum + chat.unreadCount, 0);
        setUnreadCount(total);
      } catch {
        if (isActive) {
          setUnreadCount(0);
        }
      }
    }

    refreshUnread();
    connectChatSocket();
    const unsubscribe = addChatListener(() => {
      refreshUnread();
    });

    return () => {
      isActive = false;
      unsubscribe();
    };
  }, []);

  return (
    <div className="app-shell">
      <SideNav unreadCount={unreadCount} />
      <main className="app-main">
        <div className="page-wrapper">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
