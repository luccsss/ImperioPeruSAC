export type RootType = "MEDICINE" | "DENTISTRY";
export type BookStatus = "DRAFT" | "REVIEWED" | "PUBLISHED" | "OUT_OF_STOCK" | "DISCONTINUED" | "ARCHIVED";

export type Money = { amount: number; currency: "USD" | "PEN" };
export type Seo = {
  seoTitle?: string;
  metaDescription?: string;
  canonicalUrl?: string;
  robotsPolicy?: "INDEX" | "NOINDEX";
  ogTitle?: string;
  ogDescription?: string;
  ogImageUrl?: string;
};

export type NavigationNode = {
  id: string;
  code: string;
  label: string;
  href: string;
  rootType: RootType;
  sortOrder: number;
  children: NavigationNode[];
};

export type Author = { id: string; name: string; slug: string; biography?: string; active: boolean };
export type Publisher = { id: string; organizationId: string; name: string; slug: string; description?: string; active: boolean };
export type CategorySummary = { id: string; code: string; displayName: string; rootType: RootType; primary: boolean; href: string };
export type Inventory = { id: string; onHand: number; reserved: number; available: number; minimumStock: number; status: string; version: number };
export type BookImage = { id: string; type: string; url: string; altText: string; width?: number; height?: number; sortOrder: number; authorized: boolean };

export type Book = {
  id: string;
  isbn13?: string;
  isbn10?: string;
  title: string;
  subtitle?: string;
  edition?: string;
  publicationYear?: number;
  pages?: number;
  language?: string;
  bindingFormat?: string;
  bibliographicDescription?: string;
  publisher?: Publisher;
  authors: Author[];
  sku: string;
  slug: string;
  canonicalPath: string;
  regularPrice: Money;
  promotionalPrice?: Money;
  commercialDescription?: string;
  status: BookStatus;
  featured: boolean;
  newArrival: boolean;
  categories: CategorySummary[];
  inventory?: Inventory;
  images: BookImage[];
  seo?: Seo;
};

export type Category = {
  id: string;
  parentId?: string;
  rootType: RootType;
  code: string;
  displayName: string;
  slugCandidate?: string;
  publicPath?: string;
  href: string;
  description?: string;
  sortOrder: number;
  active: boolean;
  visible: boolean;
  showInMenu: boolean;
  seo?: Seo;
};

export type ImportRow = {
  id: string;
  rowNumber: number;
  matchType?: string;
  matchedBookId?: string;
  disposition: "INVALID" | "DUPLICATE_IN_FILE" | "NEW_REQUIRES_COMMERCIAL_DATA" | "MATCHED_NO_CHANGES" | "NEEDS_REVIEW" | "APPROVED" | "REJECTED" | "APPLIED";
  validationMessages: string;
  proposedChanges: string;
};

export type ImportBatch = {
  id: string;
  sourceCode: string;
  filename: string;
  checksum: string;
  status: string;
  totalRows: number;
  validRows: number;
  errorRows: number;
  createdAt: string;
  rows: ImportRow[];
};
