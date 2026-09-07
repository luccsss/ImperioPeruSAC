import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import { AdminNav } from "@/components/admin/AdminNav";

export default async function ProtectedAdminLayout({ children }: { children: React.ReactNode }) {
  if (!(await cookies()).has("imperio_admin_token")) redirect("/admin/login");
  return <div className="admin-shell"><AdminNav /><div className="admin-main"><header className="admin-top"><div><p className="eyebrow">Consorcio Imperio Perú SAC</p><strong>Operación de catálogo</strong></div><span className="chip status-warn">Sin publicación productiva</span></header>{children}</div></div>;
}

