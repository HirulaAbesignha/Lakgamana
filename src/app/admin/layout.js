import { AdminSidebar } from '../../components/admin/AdminSidebar';

export const metadata = {
  title: 'Admin Dashboard - Lakgamana',
  description: 'Admin dashboard for managing train reservations, users, and system settings.',
};

export default function AdminLayout({ children }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <AdminSidebar />
      <div className="ml-64">
        <div className="p-8">
          {children}
        </div>
      </div>
    </div>
  );
}
